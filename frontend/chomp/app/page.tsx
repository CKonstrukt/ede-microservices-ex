import Image from 'next/image';
import logo from '@/public/images/logo.jpg';

export default function Index() {
  return (
    <div className='flex flex-col items-center'>
      <Image
        src={logo}
        alt='logo'
        width={300}
        height={300}
        className='mt-10 rounded-xl'
      />
      <h1 className='mt-5 text-3xl'>
        Start <b>Chomping</b>!
      </h1>
      <p>
        Use the login button top right to start keeping track of your recipes.
      </p>
    </div>
  );
}
