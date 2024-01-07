import { useEffect, useMemo, useState } from 'react';
import { DbRecipe } from '@/lib/api/models/recipe';
import { getRecipeById } from '@/lib/api/recipe-service';
import { Session } from 'next-auth';
import {
  amountOfPeopleValidator,
  descriptionValidator,
  durationValidator,
  ingredientsValidator,
  instructionsValidator,
  nameValidator,
} from '@/lib/helpers/manage-recipe-validation-helper';
import { useSession } from 'next-auth/react';

export function useRecipeValidation(initialRecipeId: number) {
  const { data, update } = useSession();

  const [recipe, setRecipe] = useState<DbRecipe>({
    id: 0,
    name: '',
    duration: 'PT0H0M',
    amountOfPeople: 0,
    description: '',
    instructions: [],
    user: {
      name: '',
      image: '',
    },
    ingredients: [],
    createdAt: undefined,
    updatedAt: undefined,
  });

  useEffect(() => {
    if (!initialRecipeId) return;

    if (!data?.bearer_token) return;

    getRecipeById(data?.bearer_token, initialRecipeId)
      .then((r) => setRecipe(r))
      .catch((reason) => console.log(reason));
  }, [data, initialRecipeId]);

  const validationErrorMessages = useMemo(() => {
    return [
      nameValidator(recipe.name),
      descriptionValidator(recipe.description),
      amountOfPeopleValidator(+recipe.amountOfPeople),
      durationValidator(recipe.duration),
      ingredientsValidator(recipe.ingredients),
      instructionsValidator(recipe.instructions),
    ];
  }, [recipe]);

  return {
    data,
    recipe,
    setRecipe,
    validationErrorMessages,
  };
}
