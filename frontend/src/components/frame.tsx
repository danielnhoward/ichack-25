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

        const elements: HTMLElement[] = [];
        let counter = 0;
        function traverseElement(element: HTMLElement) {
            elements.push(element);
            element.setAttribute('data-ichack-id', counter.toString());
            counter++;

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

            Array.from(element.children as Iterable<HTMLElement>).forEach(traverseElement);
        }

        traverseElement(frameWindow.document.body);

        const transforms = await getTransforms({
            html: frameWindow.document.documentElement.innerHTML,
            // text: frameWindow.document.documentElement.textContent || '',
        });

        console.log(transforms);

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
