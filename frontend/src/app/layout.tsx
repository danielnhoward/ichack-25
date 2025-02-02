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
    description: 'Our tool takes an inaccessible website and transforms it by automatically generating meaningful alt text for images and adding labels to form elements. This makes the website more user-friendly and accessible, especially for individuals who rely on screen readers.',
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
