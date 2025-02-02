'use client';

import {
    PageActions,
    PageHeader,
    PageHeaderDescription,
    PageHeaderHeading,
} from '@/components/page-header';
import {Button} from '@/components/ui/button';
import {Input} from '@/components/ui/input';
import {Label} from '@/components/ui/label';

import {useRouter} from 'next/navigation';
import {useState} from 'react';

import type {FormEvent} from 'react';


export default function HomePage() {
    const router = useRouter();

    const [url, setUrl] = useState('');

    function redirectPage(e: FormEvent) {
        e.preventDefault();

        if (url != '') {
            router.push(`/url?url=${url}`);
        }
    }

    return (
        <PageHeader className="grow">
            <PageHeaderHeading>Access Now</PageHeaderHeading>
            <PageHeaderDescription>
                Our tool is designed to make websites more accessible and user-friendly. It automatically adds descriptions to images and links using AI, helping people who use screen readers understand the content better. With our tool, creating an inclusive and welcoming online experience is simple and effortless.
            </PageHeaderDescription>
            <PageActions>
                <form className="w-full flex flex-col gap-4 items-center" onSubmit={redirectPage}>
                    <div className="w-full">
                        <Label htmlFor="url">URL:</Label>
                        <Input
                            type="url"
                            id="url"
                            placeholder="https://docsoc.co.uk"
                            value={url}
                            onChange={(event) => setUrl(event.target.value)}
                        />
                    </div>
                    <Button className="max-w-72" type="submit">Submit</Button>
                </form>
            </PageActions>
        </PageHeader>
    );
}
