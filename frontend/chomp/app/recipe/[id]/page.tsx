import { Params } from 'next/dist/shared/lib/router/utils/route-matcher';
import RecipeDetails from '@/components/recipe/recipe-details';
import { getServerSession } from 'next-auth';
import { options } from '@/app/api/auth/[...nextauth]/options';
import { redirect } from 'next/navigation';

export default async function RecipeDetailsPage({
  params,
}: {
  params: Params;
}) {
  const session = await getServerSession(options);

  if (!session) {
    redirect('/api/auth/signin?callbackurl=/home');
  }

  return <RecipeDetails recipeId={params.id} />;
}
