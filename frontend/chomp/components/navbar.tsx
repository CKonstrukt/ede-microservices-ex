'use client';

import {
  Avatar,
  Button,
  Dropdown,
  DropdownItem,
  DropdownMenu,
  DropdownTrigger,
  Link,
  Navbar,
  NavbarBrand,
  NavbarContent,
  NavbarItem,
  NavbarMenu,
  NavbarMenuItem,
  NavbarMenuToggle,
} from '@nextui-org/react';
import logo from '@/public/images/logo.jpg';
import Image from 'next/image';
import { useState } from 'react';
import { useSession } from 'next-auth/react';
import { signIn, signOut } from 'next-auth/react';

type User =
  | {
      name?: string | null | undefined;
      email?: string | null | undefined;
      image?: string | null | undefined;
    }
  | undefined;

export default function NavBar() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const { data, status } = useSession();

  const user: User = data?.user;

  return (
    <Navbar onMenuOpenChange={setIsMenuOpen} shouldHideOnScroll={true}>
      <NavbarContent>
        <NavbarMenuToggle
          aria-label={isMenuOpen ? 'Close menu' : 'Open menu'}
          className='sm:hidden'
        />
        <NavbarBrand>
          <Image
            src={logo.src}
            alt='logo'
            width={36}
            height={36}
            className='rounded'
            priority
          />
          <p className='ml-3'>Chomp!</p>
        </NavbarBrand>
      </NavbarContent>

      <NavbarContent className='gap-4' justify='end'>
        <div className='hidden gap-4 sm:flex'>
          <NavbarItem>
            <Link color='foreground' href={'/home'}>
              Home
            </Link>
          </NavbarItem>
          <NavbarItem>
            <Link color='foreground' href={'/recipe/manage/create'}>
              Create
            </Link>
          </NavbarItem>
        </div>
        {status === 'authenticated' ? (
          <Dropdown placement='bottom-end'>
            <DropdownTrigger>
              {user?.image ? (
                <Avatar
                  isBordered
                  as='button'
                  className='transition-transform'
                  color='secondary'
                  name={user.name || 'placeholder'}
                  size='sm'
                  src={user.image}
                />
              ) : undefined}
            </DropdownTrigger>
            <DropdownMenu aria-label='Profile Actions' variant='flat'>
              <DropdownItem
                key='profile'
                textValue='Info'
                className='h-14 gap-2'
              >
                <p className='font-semibold'>Signed in as</p>
                <p className='font-semibold'>{user?.email}</p>
              </DropdownItem>
              <DropdownItem
                key='logout'
                color='danger'
                textValue='Log Out'
                onClick={() => signOut({ callbackUrl: '/' })}
              >
                Log Out
              </DropdownItem>
            </DropdownMenu>
          </Dropdown>
        ) : (
          <Button
            color='primary'
            onClick={() =>
              signIn('google', {
                callbackUrl: '/home',
              })
            }
          >
            Sign in
          </Button>
        )}
      </NavbarContent>

      <NavbarMenu>
        <NavbarMenuItem>
          <Link color='foreground' href={'/home'} className='w-full' size='lg'>
            Home
          </Link>
          <Link
            color='foreground'
            href={'/recipe/manage/create'}
            className='w-full'
            size='lg'
          >
            Create
          </Link>
        </NavbarMenuItem>
      </NavbarMenu>
    </Navbar>
  );
}
