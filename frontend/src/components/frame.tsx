'use client';

import {getTransforms} from '@/server/backend';

import {useRef} from 'react';

export default function Frame({url}: {url: string}) {
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

        transforms.forEach((transform) => {
            switch (transform.type) {
            case 'image': {
                elements[transform.id].alt = transform.alt;
                elements[transform.id].title = transform.alt;
            }
            }
        });
    }

    return <iframe
        className="w-full h-full"
        src={`/proxy?url=${url}`}
        ref={frameRef}
        onLoad={onFrameLoad}
    />;
}
