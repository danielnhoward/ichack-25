interface ImageTransform {
    alt: string;
}

interface LabelTransform {
    alt: string;
}

interface ButtonTransform {

}

type Transform = {
    id: string;
} & (({type: 'image'} & ImageTransform) | ({type: 'link'} & LabelTransform) | ({type: 'button'} & ButtonTransform))

interface Input {
    html: string;
    // text: string;
}
