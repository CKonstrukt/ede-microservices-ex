import MyRecipes from '@/components/recipe/my-recipes';
import { Button, Link } from '@nextui-org/react';
import { getServerSession } from 'next-auth';
import { options } from '@/app/api/auth/[...nextauth]/options';
import { redirect } from 'next/navigation';

export default async function HomePage() {
  const session = await getServerSession(options);

  if (!session) {
    redirect('/api/auth/signin?callbackurl=/home');
  }

  return (
    <div>
      <div className='mb-8 flex justify-between'>
        <h1 className='text-3xl'>My recipes</h1>

        <Button
          as={Link}
          href={'/recipe/manage/create'}
          className='ml-5'
          color='primary'
        >
          Create new
        </Button>
      </div>
      <MyRecipes />
    </div>
  );
}
