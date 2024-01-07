import type { Metadata } from 'next';
import { Providers } from './providers';
import { Inter } from 'next/font/google';
import './globals.css';
import NavBar from '@/components/navbar';
import AuthProvider from './context/AuthProvider';
import React from 'react';

const inter = Inter({ subsets: ['latin'] });

export const metadata: Metadata = {
  title: 'Chomp',
  description: 'Create and share your favourite recipes!',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang='en' className='light'>
      <body className={inter.className}>
        <AuthProvider>
          <Providers>
            <header>
              <NavBar />
            </header>
            <main className='flex justify-center'>
              <div className='mt-10 w-full px-6 lg:w-1/2 lg:p-0'>
                {children}
              </div>
            </main>
          </Providers>
        </AuthProvider>
      </body>
    </html>
  );
}
