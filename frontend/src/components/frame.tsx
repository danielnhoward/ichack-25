'use client';

import Sidebar from './sidebar';
import {SidebarProvider, SidebarTrigger} from './ui/sidebar';
import {getTransforms} from '@/server/backend';

import {Loader2} from 'lucide-react';
import {useRef, useState} from 'react';

export default function Frame({url}: {url: string}) {
    const [loaded, setLoaded] = useState(false);
    const [transformsState, setTransformsState] = useState<Transform[] | null>(null);
    const [elementsState, setElementsState] = useState<HTMLElement[] | null>(null);
    const frameRef = useRef<HTMLIFrameElement>(null);

    async function onFrameLoad() {
        const frame = frameRef.current;
        if (frame === null) return;

        const frameWindow = frame.contentWindow;
        if (frameWindow === null) throw new Error('iframe window is null');

        const clonedDocument = frameWindow.document.cloneNode(true) as Document;

        const elements: HTMLElement[] = [];
        let counter = 0;
        function traverseElement(element: HTMLElement, extractStyles: boolean) {
            if (!extractStyles) elements.push(element);
            element.setAttribute('data-ichack-id', counter.toString());
            counter++;

            if (extractStyles) {
                const styles = getComputedStyle(element);
                const emptyElement = document.createElement(element.tagName);
                frameWindow?.document.body.appendChild(emptyElement);
                const defaultStyles = getComputedStyle(emptyElement);

                Object.values(styles).forEach((key) => {
                    // @ts-expect-error Types aren't set correctly
                    if (styles[key] !== defaultStyles[key]) {
                        // @ts-expect-error Types aren't set correctly
                        element.style[key] = styles[key];
                    }
                });

                frameWindow?.document.body.removeChild(emptyElement);
            }

            Array.from(element.children as Iterable<HTMLElement>).forEach((e) => {
                traverseElement(e, extractStyles);
            });
        }

        traverseElement(clonedDocument.body, true);
        counter = 0;
        traverseElement(frameWindow.document.body, false);

        const transforms = await getTransforms({
            html: clonedDocument.documentElement.innerHTML,
            // text: clonedDocument.documentElement.textContent || '',
        });

        setTransformsState(transforms);

        transforms.forEach((transform) => {
            const id = parseInt(transform.id);
            switch (transform.type) {
            case 'image': {
                console.log(transform, elements);
                const image = elements[id] as HTMLImageElement;
                image.alt = transform.alt;
                image.title = transform.alt;
                break;
            }
            case 'link': {
                const label = elements[id] as HTMLLabelElement;
                label.ariaLabel = transform.alt;
                break;
            }
            case 'button': {
                const button = document.createElement('button');
                const div = elements[id] as HTMLDivElement;
                button.innerHTML = div.innerText;

                for (const attr of div.attributes) {
                    button.setAttribute(attr.name, attr.value);
                }
                div.replaceWith(button);
                break;
            }
            case 'language': {
                document.documentElement.lang = transform.lang;
                break;
            }
        }
        });

        setLoaded(true);
        setElementsState(elements);
    }

    return (
        <SidebarProvider>
            {transformsState === null || elementsState === null ? <></> : <Sidebar transforms={transformsState} elements={elementsState}/>}
            <main className="w-full flex flex-col">
                <div className={`flex items-center justify-between ${loaded ? '' : 'hidden'}`}>
                    <SidebarTrigger/>
                    <p className="m-3">
                        Found <span className={transformsState?.length === 0 ? 'text-green-600' : 'text-red-600'}>{transformsState?.length}</span> issue{transformsState?.length === 1 ? '' : 's'}
                    </p>
                    <p className="m-3"><b>Access Now</b></p>
                </div>
                <iframe
                    title="Site Iframe"
                    className={`w-full h-full ${loaded ? '' : 'hidden'}`}
                    src={`/proxy?url=${url}`}
                    ref={frameRef}
                    onLoad={onFrameLoad}
                />
                <div className={`flex justify-center items-center min-h-[100vh] ${loaded ? 'hidden' : ''}`}>
                    <Loader2 className="animate-spin"/>
                </div>
            </main>
        </SidebarProvider>
    );
}
