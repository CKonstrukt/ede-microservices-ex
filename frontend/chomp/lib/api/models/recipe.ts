import { DateTime } from 'luxon';

export interface DbRecipe {
  id: number;
  name: string;
  duration: string;
  amountOfPeople: number;
  description?: string;
  instructions: string[];
  user?: DbRecipeUser;
  ingredients: DbIngredient[];
  createdAt?: DateTime;
  updatedAt?: DateTime;
}

export interface DbRecipeUser {
  name: string;
  image: string;
}
