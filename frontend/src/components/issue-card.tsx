import {Card, CardHeader, CardTitle, CardDescription} from './ui/card';

export default function IssueCard({transform, element}: {transform: Transform, element: HTMLElement}) {
    return (
        <Card className="my-2">
            <CardHeader>
                <CardTitle>{getTitle(transform.type)}</CardTitle>
                <CardDescription>{getDescriptions(transform, element)}</CardDescription>
            </CardHeader>
        </Card>
    );
}

function getTitle(type: 'image' | 'link' | 'button') {
    switch (type) {
    case 'image':
        return <>Images should always contain an <code>alt</code> property</>;
    case 'link':
        return <>Links should always contain <code>ariaLabel</code> property</>;
    case 'button':
        return <>All clickable <code>div</code> elements should instead use a <code>button</code></>;
    }
}

function getDescriptions(transform: Transform, element: HTMLElement) {
    switch (transform.type) {
    case 'image': {
        const image = element as HTMLImageElement;
        return <>
            When a screen reader attempts to read an image, or an image fails to load,
            the <code>alt</code> property is used to tell the browser what is in the image.
            We have identified that the following image does not have an <code>alt</code> property.

            <img src={image.src} alt="Image that requires an alt text" className="w-full my-4"/>

            We think that a suitable <code>alt</code> value for this could be &quot;<i>{transform.alt}</i>&quot;.
        </>;
    }
    case 'link': {
        const link = element as HTMLAnchorElement;
        return <>
            When a screen reader attempts to read a link, it sometimes fails to find its definition. By setting
            <code>areaLabel</code>, the screen reader is able to get a definition for the link to read out to the
            user. We have detected that the link to the site below does not have this property.

            <code>{link.href}</code>

            We think that a suitable <code>alt</code> value for this could be &quot;<i>{transform.alt}</i>&quot;.
        </>;
    }
    case 'button':
        return <>All clickable <code>div</code> elements should instead use a <code>button</code></>;
    }
}
