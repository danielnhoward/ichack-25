interface ImageTransform {
    alt: string;
}

interface LabelTransform {
    for: string;
}

type Transform = {
    id: string;
} & ({type: 'image'} & ImageTransform) | ({type: 'label'} & LabelTransform)

interface Input {
    html: string;
    // text: string;
}
