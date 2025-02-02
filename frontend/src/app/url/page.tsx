import Frame from '@/components/frame';

type SearchParams = { [key: string]: string | string[] | undefined }

export default async function Url(props: {searchParams: Promise<SearchParams>}) {
    const searchParams = await props.searchParams;
    const url = searchParams['url'] as string;

    return (
        <div className="w-full h-full">
            <Frame url={url}/>
        </div>
    );
}
