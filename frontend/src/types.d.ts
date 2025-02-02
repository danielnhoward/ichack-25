interface ImageTransform {
    alt: string;
}

interface LabelTransform {
    alt: string;
}

interface LangTransform {
    lang: string;
}

type Transform = {
    id: string;
} & (({type: 'image'} & ImageTransform) | ({type: 'link'} & LabelTransform) | {type: 'button'} | ({type: 'language'} & LangTransform))

interface Input {
    html: string;
    // text: string;
}
