import Sidebar from '@/components/sidebar';
import {SidebarProvider, SidebarTrigger} from '@/components/ui/sidebar';


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
    title: 'Page Accesability Improver',
    description: 'Page Accesability Improver',
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
