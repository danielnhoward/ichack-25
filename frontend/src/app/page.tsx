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


export default function HomePage() {
    const router = useRouter();

    const [url, setUrl] = useState('');

    function redirectPage() {
        if (url != '') {
            router.push(`/url?url=${url}`);
        }
    }

    return (
        <PageHeader className="grow">
            <PageHeaderHeading>Access Now</PageHeaderHeading>
            <PageHeaderDescription>
                Insert Description
            </PageHeaderDescription>
            <PageActions className="flex flex-col gap-4">
                <div className="w-full">
                    <Label htmlFor="url">URL:</Label>
                    <Input
                        type="url"
                        id="url"
                        placeholder="docsoc.co.uk"
                        onChange={(event) => setUrl(event.target.value)}
                    />
                </div>
                <Button onClick={redirectPage}>Submit</Button>
            </PageActions>
        </PageHeader>
    );
}
