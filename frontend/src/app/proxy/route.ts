import type {NextRequest} from 'next/server';

function replaceUrl(url?: string): string {
    return `${process.env.ORIGIN}/proxy?url=${url === undefined ? '' : encodeURI(url)}`;
}

const INJECTED_SCRIPT = `<script src="/injected.js"></script>`;

const ALLOWED_HEADERS = [
    'content-type',
    'Content-Type',
];

const IMAGE_ALLOWED_HEADERS = [
    'content-length',
    'accept-ranges',
];

function isHtml(res: Response): boolean {
    return res.headers.get('content-type')?.startsWith('text/html') ||
        res.headers.get('Content-Type')?.startsWith('text/html') ||
        false;
}

function isImage(res: Response): boolean {
    return res.headers.get('content-type')?.startsWith('image') ||
        res.headers.get('Content-Type')?.startsWith('image') ||
        false;
}

function modifyHeader(value: string, key: string, image: boolean): string | null {
    switch (key) {
    case 'location':
        return replaceUrl(value);
    }

    return ALLOWED_HEADERS.includes(key) || (image && IMAGE_ALLOWED_HEADERS.includes(key)) ? value : null;
}

function procHeaders(headers: Headers, html: boolean): HeadersInit {
    const newHeaders: Record<string, string> = {};
    headers.forEach((value, key) => {
        const newValue = modifyHeader(value, key, html);
        if (newValue === null) return;
        newHeaders[key] = newValue;
    });
    return newHeaders;
}

function procBody(body: string, url: URL): string {
    return body
        .replaceAll(/((href|src)=("|'))\/\/(.*?("|'))/g, `$1http://$4`)
        .replaceAll(/((href|src)=("|'))(http.*?("|'))/g, `$1${replaceUrl()}$4`)
        .replaceAll(/((href|src)=)("|')([^h].*?)("|')/g, `$1"${replaceUrl(url.origin)}$4"`)
        .replace(/(<body.*?>)/, `$1${INJECTED_SCRIPT}`);
}

async function getBody(res: Response, url: string): Promise<BodyInit | null> {
    if (isHtml(res)) {
        const text = await res.text();
        return procBody(text, new URL(url));
    } else {
        return res.body;
    }
}

export async function GET(req: NextRequest) {
    const url = req.nextUrl.searchParams.get('url');
    if (url === null) {
        throw new Error('url parameter is null');
    }

    const proxyRes = await fetch(url, {
        method: 'GET',
        redirect: 'manual',
        headers: procHeaders(req.headers, false),
    });
    const body = await getBody(proxyRes, url);

    return new Response(body, {
        status: proxyRes.status,
        headers: procHeaders(proxyRes.headers, isImage(proxyRes)),
    });
}
