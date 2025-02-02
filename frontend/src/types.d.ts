interface ImageTransform {
    alt: string;
}

interface LabelTransform {
    alt: string;
}

type Transform = {
    id: string;
} & (({type: 'image'} & ImageTransform) | ({type: 'link'} & LabelTransform))

interface Input {
    html: string;
    // text: string;
}
