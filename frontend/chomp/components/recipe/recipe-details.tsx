'use client';

import { useSession } from 'next-auth/react';
import { useEffect, useState } from 'react';
import { DbRecipe } from '@/lib/api/models/recipe';
import { getRecipeById } from '@/lib/api/recipe-service';
import { notFound } from 'next/navigation';
import { Button, Link, Spinner } from '@nextui-org/react';
import {
  extractHours,
  extractMinutes,
} from '@/lib/helpers/recipe-duration-helper';
import { FaRegClock, FaUserGroup } from 'react-icons/fa6';

export default function RecipeDetails({ recipeId }: { recipeId: number }) {
  const { data } = useSession();
  const [recipe, setRecipe] = useState<DbRecipe | undefined>(undefined);

  useEffect(() => {
    if (!data?.bearer_token) return;

    getRecipeById(data?.bearer_token, recipeId)
      .then((r) => setRecipe(r))
      .catch((reason) => {
        console.log(reason);
        notFound();
      });
  }, [data, recipeId]);

  if (!recipe) return <Spinner />;

  let durationHours = extractHours(recipe.duration);
  let durationMinutes = extractMinutes(recipe.duration);

  if (durationHours) {
    durationHours += 'h';
  }

  if (durationMinutes) {
    if (durationHours) {
      durationMinutes = ' ' + durationMinutes + 'm';
    } else {
      durationMinutes += 'm';
    }
  }

  return (
    <div>
      <Link href={'/home'} className='mb-5'>
        &lt; Go back
      </Link>
      <div className='flex justify-between'>
        <h1 className='text-3xl'>{recipe.name}</h1>
        <Button as={Link} href={`/recipe/manage/${recipe.id}`}>
          Edit
        </Button>
      </div>
      <h3 className='mt-1 text-xl'>{recipe.description}</h3>
      <div className='mt-3 flex items-center gap-2'>
        <FaUserGroup />
        <p>For {recipe.amountOfPeople} people</p>
      </div>
      <div className='mt-1 flex items-center gap-2'>
        <FaRegClock />
        <p>
          Takes about {durationHours}
          {durationMinutes}
        </p>
      </div>

      <h2 className='mt-6 text-2xl'>Needed ingredients</h2>
      <ul className='mt-2 list-disc'>
        {recipe.ingredients.map((ingredient) => (
          <li key={ingredient.id} className='ml-5 mt-2'>
            {ingredient.quantity} {ingredient.unit} of {ingredient.name}
          </li>
        ))}
      </ul>

      <h2 className='mt-6 text-2xl'>Steps</h2>
      <ol className='list-decimal'>
        {recipe.instructions.map((instruction, idx) => (
          <li key={idx} className='ml-5 mt-2'>
            {instruction}
          </li>
        ))}
      </ol>
    </div>
  );
}
