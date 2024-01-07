import axios from 'axios';
import { DbRecipe } from './models/recipe';
import { DateTime } from 'luxon';
import { mapToCorrectFormat } from '@/lib/helpers/recipe-duration-helper';

function mapResponseToValidRecipe(recipe: DbRecipe): DbRecipe {
  return {
    ...recipe,
    duration: mapToCorrectFormat(recipe.duration),
    createdAt: DateTime.fromISO(recipe.createdAt!.toString()),
    updatedAt: DateTime.fromISO(recipe.updatedAt!.toString()),
  };
}

export async function createRecipe(bearer_token: string, recipe: DbRecipe) {
  const { data } = await axios.post<DbRecipe>(
    `${process.env.NEXT_PUBLIC_API_BASE_URL}/recipe`,
    recipe,
    {
      headers: { Authorization: `Bearer ${bearer_token}` },
    }
  );
  return mapResponseToValidRecipe(data);
}

export async function getRecipeById(bearer_token: string, id: number) {
  const { data } = await axios.get<DbRecipe>(
    `${process.env.NEXT_PUBLIC_API_BASE_URL}/recipe/${id}`,
    { headers: { Authorization: `Bearer ${bearer_token}` } }
  );

  return mapResponseToValidRecipe(data);
}

export async function getRecipeForUserSelf(bearer_token: string) {
  const { data } = await axios.get<DbRecipe[]>(
    `${process.env.NEXT_PUBLIC_API_BASE_URL}/recipe/me`,
    { headers: { Authorization: `Bearer ${bearer_token}` } }
  );

  return data.map((recipe) => mapResponseToValidRecipe(recipe));
}

export async function updateRecipe(bearer_token: string, recipe: DbRecipe) {
  const { data } = await axios.put<DbRecipe>(
    `${process.env.NEXT_PUBLIC_API_BASE_URL}/recipe/${recipe.id}`,
    recipe,
    {
      headers: { Authorization: `Bearer ${bearer_token}` },
    }
  );

  return mapResponseToValidRecipe(data);
}

export async function deleteRecipe(bearer_token: string, id: number) {
  await axios.delete(`${process.env.NEXT_PUBLIC_API_BASE_URL}/recipe/${id}`, {
    headers: { Authorization: `Bearer ${bearer_token}` },
  });
}
