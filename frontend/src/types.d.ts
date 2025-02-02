interface ImageTransform {
    alt: string;
}

interface LabelTransform {
    alt: string;
}

interface ButtonTransform {

}

interface LangTransform {
    lang: string;
}

type Transform = {
    id: string;
} & (({type: 'image'} & ImageTransform) | ({type: 'link'} & LabelTransform) | ({type: 'button'} & ButtonTransform) | ({type: 'language'} & LangTransform))

interface Input {
    html: string;
    // text: string;
}
