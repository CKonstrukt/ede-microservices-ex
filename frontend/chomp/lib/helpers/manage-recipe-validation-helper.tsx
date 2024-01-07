import {
  extractHours,
  extractMinutes,
} from '@/lib/helpers/recipe-duration-helper';

export function nameValidator(value: string): string {
  if (value === '') {
    return 'Name is required';
  }

  return '';
}

export function descriptionValidator(value?: string): string {
  if (!value) {
    return '';
  }

  if (value.length >= 255) {
    return "Description can't be longer than 255 characters";
  }

  return '';
}

export function amountOfPeopleValidator(value: number): string {
  const re = new RegExp('^\\d+$');

  if (value.toString() === '') {
    return 'Amount of people is required';
  }

  if (value === 0) {
    return "Amount of people can't be 0";
  }

  if (!re.test(value.toString())) {
    return 'Amount of people must be an integer';
  }

  return '';
}

export function durationValidator(value: string): string {
  const re = new RegExp('^\\d+$');

  if (value === 'PT0H0M') {
    return 'Either an hour or a minute is required';
  }

  const hours = extractHours(value);
  if (hours && (!re.test(hours) || +hours < 1 || +hours > 50)) {
    return 'Hours must be an integer between 1 and 50';
  }

  const minutes = extractMinutes(value);
  if (minutes && (!re.test(minutes) || +minutes < 1 || +minutes > 59)) {
    return 'Minutes must be an integer between 1 and 59';
  }

  return '';
}

export function ingredientsValidator(ingredients: DbIngredient[]): string {
  if (ingredients.length === 0) {
    return 'Ingredients are required';
  }

  if (ingredients.some((i) => i.unit === undefined || i.unit === '')) {
    return 'An ingredient needs to have a unit';
  }

  if (ingredients.some((i) => i.quantity === undefined || +i.quantity <= 0)) {
    return 'An ingredient needs to have a quantity and it should be larger than 0';
  }

  return '';
}

export function instructionsValidator(instructions: string[]): string {
  if (instructions.length === 0) {
    return 'Instructions are required';
  }

  return '';
}
