interface ImageTransform {
    alt: string;
}

interface LabelTransform {
    for: string;
}

type Transform = ({type: 'image'} & ImageTransform) | ({type: 'label'} & LabelTransform)

interface Input {
    html: string;
    text: string;
}
