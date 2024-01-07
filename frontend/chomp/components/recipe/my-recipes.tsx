'use client';

import { deleteRecipe, getRecipeForUserSelf } from '@/lib/api/recipe-service';
import { Session } from 'next-auth';
import { useSession } from 'next-auth/react';
import { useEffect, useState } from 'react';
import { DbRecipe } from '@/lib/api/models/recipe';
import RecipeCard from './recipe-card';
import { Button, Input, Spinner } from '@nextui-org/react';

export default function MyRecipes() {
  const { data } = useSession();
  const [recipes, setRecipes] = useState<DbRecipe[]>([]);
  const [searchFilter, setSearchFilter] = useState('');

  useEffect(() => {
    if (!data?.bearer_token) return;

    getRecipeForUserSelf(data?.bearer_token).then((res) => setRecipes(res));
  }, [data]);

  if (!data) {
    return (
      <div>
        <Spinner />
      </div>
    );
  }

  function handleDelete(id: number) {
    if (!data?.bearer_token) return;

    deleteRecipe(data?.bearer_token, id).then(() =>
      setRecipes((prevState) => prevState.filter((recipe) => recipe.id !== id))
    );
  }

  const filteredRecipes = recipes
    .filter((r) => r.name.toLowerCase().includes(searchFilter.toLowerCase()))
    .sort((a, b) => b.updatedAt?.toMillis()! - a.updatedAt?.toMillis()!);
  return (
    <div>
      <Input
        label='search'
        isClearable
        value={searchFilter}
        onValueChange={setSearchFilter}
      />

      {filteredRecipes.length !== 0 ? (
        <div className='mt-6 grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3'>
          {filteredRecipes.map((r) => (
            <RecipeCard key={r.id} recipe={r} handleDelete={handleDelete} />
          ))}
        </div>
      ) : (
        <p>No recipes matching your filter.</p>
      )}
    </div>
  );
}
