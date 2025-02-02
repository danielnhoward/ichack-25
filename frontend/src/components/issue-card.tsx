import {Card, CardHeader, CardTitle, CardDescription} from './ui/card';

import {
    Accordion,
    AccordionContent,
    AccordionItem,
    AccordionTrigger,
  } from "./ui/accordion"
  

export default function IssueCard({transform, element}: {transform: Transform, element: HTMLElement}) {
    return (
        <Card className="my-2">
            <CardHeader
                className="cursor-pointer" style= {{padding: "0.1rem", paddingLeft: "0.1rem"}}
            >
                <Accordion type="single" collapsible className='border-0'>
                <AccordionItem value="item-1" className='border-0'>
                <AccordionTrigger>{getTitle(transform.type)}</AccordionTrigger>
                <AccordionContent>
                {getDescriptions(transform, element)}
                </AccordionContent>
                </AccordionItem>
                </Accordion>
            </CardHeader>
        </Card>
            
        
    );
}

function getTitle(type: 'image' | 'link' | 'button' | 'language') {
    switch (type) {
    case 'image':
        return <>Images should always contain an <code>alt</code> property</>;
    case 'link':
        return <>Links should always contain <code>ariaLabel</code> property</>;
    case 'button':
        return <>All clickable <code>div</code> elements should instead use a <code>button</code></>;
    case 'language':
        return <>The website should set it's language</>
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
        return <>When a screen reader attempts to read a clickable <code>div</code>, it fails to announce to the user that the 
        <code>div</code>. It should instead use a <code>button</code> for it's action to be clearer</>;

    case 'language':
        return <>The website should set the language of content in it's html tag. We have automatically detected the language to be: <code>&lt;html lang="{transform.lang}"&gt;</code>
</>; 
}