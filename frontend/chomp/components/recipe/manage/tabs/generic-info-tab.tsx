import {
  Button,
  Card,
  CardBody,
  CardFooter,
  Input,
  Textarea,
} from '@nextui-org/react';
import { ChangeEvent, useState } from 'react';
import { DbRecipe } from '@/lib/api/models/recipe';
import { DateTime } from 'luxon';
import {
  extractHours,
  extractMinutes,
} from '@/lib/helpers/recipe-duration-helper';

export default function GenericInfoTab(props: {
  recipe: DbRecipe;
  validationErrorMessages: string[];
  handleInputChange: (arg0: ChangeEvent<HTMLInputElement>) => void;
  handleHourChange: (arg0: string) => void;
  handleMinuteChange: (arg0: string) => void;
}) {
  const nameErrorMessage = props.validationErrorMessages[0];
  const descriptionErrorMessage = props.validationErrorMessages[1];
  const amountOfPeopleErrorMessage = props.validationErrorMessages[2];
  const durationErrorMessage = props.validationErrorMessages[3];

  return (
    <Card>
      <CardBody>
        <Input
          type='text'
          name='name'
          label='Name'
          isRequired
          isInvalid={!!nameErrorMessage}
          color={!!nameErrorMessage ? 'danger' : 'default'}
          errorMessage={nameErrorMessage}
          value={props.recipe.name}
          onChange={props.handleInputChange}
        />
        <Textarea
          name='description'
          label='Description'
          isInvalid={!!descriptionErrorMessage}
          color={!!descriptionErrorMessage ? 'danger' : 'default'}
          errorMessage={descriptionErrorMessage}
          value={props.recipe.description}
          onChange={props.handleInputChange}
          className='mt-2'
        />
        <Input
          type='number'
          name='amountOfPeople'
          label='Amount of people'
          isRequired
          isInvalid={!!amountOfPeopleErrorMessage}
          color={!!amountOfPeopleErrorMessage ? 'danger' : 'default'}
          errorMessage={amountOfPeopleErrorMessage}
          value={props.recipe.amountOfPeople.toString()}
          onChange={props.handleInputChange}
          className='mt-2'
        />
        <p className='mt-4'>Duration</p>
        <div className='flex gap-4'>
          <Input
            type='number'
            label='Hours'
            isRequired
            isInvalid={!!durationErrorMessage}
            color={!!durationErrorMessage ? 'danger' : 'default'}
            errorMessage={durationErrorMessage}
            value={extractHours(props.recipe.duration)}
            onValueChange={props.handleHourChange}
          />
          <Input
            type='number'
            label='Minutes'
            isRequired
            isInvalid={!!durationErrorMessage}
            color={!!durationErrorMessage ? 'danger' : 'default'}
            value={extractMinutes(props.recipe.duration)}
            onValueChange={props.handleMinuteChange}
          />
        </div>
      </CardBody>
    </Card>
  );
}
