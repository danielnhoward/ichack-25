// eslint-disable-next-line camelcase
import {Geist, Geist_Mono} from 'next/font/google';

import type {Metadata} from 'next';

import './globals.css';

const geistSans = Geist({
    variable: '--font-geist-sans',
    subsets: ['latin'],
});

const geistMono = Geist_Mono({
    variable: '--font-geist-mono',
    subsets: ['latin'],
});

export const metadata: Metadata = {
    title: 'Access Now',
    description: 'Our tool is designed to make websites more accessible and user-friendly. It automatically adds descriptions to images and links using AI, helping people who use screen readers understand the content better. With our tool, creating an inclusive and welcoming online experience is simple and effortless.',
};

export default function RootLayout({
    children,
}: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <html lang="en">
            <body
                className={`${geistSans.variable} ${geistMono.variable} antialiased`}
            >
                {children}
            </body>
        </html>
    );
}
