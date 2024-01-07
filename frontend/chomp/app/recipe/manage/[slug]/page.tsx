import ManageRecipe from '@/components/recipe/manage/manage-recipe';
import { Params } from 'next/dist/shared/lib/router/utils/route-matcher';
import { notFound, redirect } from 'next/navigation';
import { getServerSession } from 'next-auth';
import { options } from '@/app/api/auth/[...nextauth]/options';

export default async function ManageRecipePage({ params }: { params: Params }) {
  const session = await getServerSession(options);

  if (!session) {
    redirect('/api/auth/signin?callbackurl=/home');
  }

  const re = new RegExp('^\\d+$');

  if (re.test(params.slug)) {
    return <ManageRecipe initialRecipeId={+params.slug} />;
  }

  if (params.slug === 'create') {
    return <ManageRecipe initialRecipeId={0} />;
  }

  notFound();
}
