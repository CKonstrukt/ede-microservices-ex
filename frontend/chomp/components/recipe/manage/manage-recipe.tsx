'use client';

import { useSession } from 'next-auth/react';
import { useRouter } from 'next/navigation';
import { ChangeEvent, useState } from 'react';
import {
  extractHours,
  extractMinutes,
} from '@/lib/helpers/recipe-duration-helper';
import { createRecipe, updateRecipe } from '@/lib/api/recipe-service';
import { Button, Tab, Tabs } from '@nextui-org/react';
import GenericInfoTab from '@/components/recipe/manage/tabs/generic-info-tab';
import IngredientsTab from '@/components/recipe/manage/tabs/ingredients-tab';
import InstructionsTab from '@/components/recipe/manage/tabs/instructions-tab';
import { useRecipeValidation } from '@/hooks/recipe-validation-hook';
import { Key } from '@react-types/shared';

const tabs = ['General info', 'Select ingredients', 'Instructions'];

function NavigationButton(props: {
  validationErrorMessages: string[];
  onPress: () => void;
  shouldGoNext: boolean;
}) {
  return (
    <Button
      isDisabled={
        props.shouldGoNext
          ? props.validationErrorMessages.some((e) => e !== '')
          : false
      }
      onPress={props.onPress}
    >
      {props.shouldGoNext ? 'Next' : 'Previous'}
    </Button>
  );
}

export default function ManageRecipe({
  initialRecipeId,
}: {
  initialRecipeId: number;
}) {
  const router = useRouter();

  const [selectedTab, setSelectedTab] = useState(tabs[0]);

  const { data, recipe, setRecipe, validationErrorMessages } =
    useRecipeValidation(initialRecipeId);

  const genericInfoTabErrorMessages = validationErrorMessages.slice(0, 4);
  const ingredientsTabErrorMessage = validationErrorMessages[4];
  const instructionsTabErrorMessage = validationErrorMessages[5];

  function handleInputChange(event: ChangeEvent<HTMLInputElement>) {
    const name = event.target.name;
    const value = event.target.value;

    setRecipe((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  }

  function handleHourChange(value: string) {
    setRecipe((prevState) => {
      const minutes = extractMinutes(prevState.duration);

      if (!value) {
        return {
          ...prevState,
          duration: `PT0H${minutes ? minutes : '0'}M`,
        };
      }

      const newDuration = `PT${value}H${minutes ? minutes : '0'}M`;
      return {
        ...prevState,
        duration: newDuration,
      };
    });
  }

  function handleMinuteChange(value: string) {
    setRecipe((prevState) => {
      const hours = extractHours(prevState.duration);

      if (!value) {
        return {
          ...prevState,
          duration: `PT${hours ? hours : '0'}H0M`,
        };
      }

      const newDuration = `PT${hours ? hours : '0'}H${value}M`;
      return {
        ...prevState,
        duration: newDuration,
      };
    });
  }

  function handleIngredientAdd(ingredient: DbIngredient) {
    setRecipe((prevState) => ({
      ...prevState,
      ingredients: [...prevState.ingredients, ingredient],
    }));
  }

  function handleIngredientQuantityChange(id: string, quantity: number) {
    setRecipe((prevState) => {
      const newIngredients = prevState.ingredients.map((ingredient) =>
        ingredient.id === id ? { ...ingredient, quantity } : ingredient
      );

      return {
        ...prevState,
        ingredients: newIngredients,
      };
    });
  }

  function handleIngredientUnitChange(id: string, unit: string) {
    setRecipe((prevState) => {
      const newIngredients = prevState.ingredients.map((ingredient) =>
        ingredient.id === id ? { ...ingredient, unit } : ingredient
      );

      return {
        ...prevState,
        ingredients: newIngredients,
      };
    });
  }

  function handleIngredientDelete(id: string) {
    setRecipe((prevState) => ({
      ...prevState,
      ingredients: prevState.ingredients.filter(
        (ingredient) => ingredient.id !== id
      ),
    }));
  }

  function handleAddInstruction(newInstruction: string) {
    setRecipe((prevState) => ({
      ...prevState,
      instructions: [...prevState.instructions, newInstruction],
    }));
  }

  function handleUpdateInstruction(idx: number, updatedInstruction: string) {
    setRecipe((prevState) => {
      const newInstructions = [...prevState.instructions];
      newInstructions[idx] = updatedInstruction;

      return {
        ...prevState,
        instructions: newInstructions,
      };
    });
  }

  function handleDeleteInstruction(indexToDelete: number) {
    setRecipe((prevState) => ({
      ...prevState,
      instructions: prevState.instructions.filter(
        (_, idx) => idx !== indexToDelete
      ),
    }));
  }

  function handleSubmitRecipe() {
    if (!data?.bearer_token) return;

    if (initialRecipeId) {
      updateRecipe(data?.bearer_token, recipe).then(() => router.push('/home'));
    } else {
      createRecipe(data?.bearer_token, recipe).then(() => router.push('/home'));
    }
  }

  function handleTabSelectionChange(key: Key) {
    const newTab = tabs.find((t) => t === key);

    if (!newTab) return;

    setSelectedTab(newTab);
  }

  function handleNextTab() {
    const currentTabIdx = tabs.indexOf(selectedTab);
    setSelectedTab(tabs[currentTabIdx + 1]);
  }

  function handlePreviousTab() {
    const currentTabIdx = tabs.indexOf(selectedTab);
    setSelectedTab(tabs[currentTabIdx - 1]);
  }

  function getDisabledTabKeys() {
    let list = [];

    if (
      genericInfoTabErrorMessages.some((e) => e !== '') &&
      selectedTab !== tabs[0]
    ) {
      list.push(tabs[0]);
    }

    if (ingredientsTabErrorMessage !== '' && selectedTab !== tabs[1]) {
      list.push(tabs[1]);
    }

    if (instructionsTabErrorMessage !== '' && selectedTab !== tabs[2]) {
      list.push(tabs[2]);
    }

    return list;
  }

  return (
    <>
      <Tabs
        disabledKeys={getDisabledTabKeys()}
        selectedKey={selectedTab}
        onSelectionChange={handleTabSelectionChange}
        fullWidth
      >
        <Tab key={tabs[0]} title={tabs[0]}>
          <>
            <GenericInfoTab
              recipe={recipe}
              validationErrorMessages={genericInfoTabErrorMessages}
              handleInputChange={handleInputChange}
              handleHourChange={handleHourChange}
              handleMinuteChange={handleMinuteChange}
            />
            <div className='mt-3 flex justify-end'>
              <NavigationButton
                validationErrorMessages={genericInfoTabErrorMessages}
                onPress={handleNextTab}
                shouldGoNext={true}
              />
            </div>
          </>
        </Tab>
        <Tab key={tabs[1]} title={tabs[1]}>
          <>
            <IngredientsTab
              data={data}
              selectedIngredients={recipe.ingredients}
              validationErrorMessage={ingredientsTabErrorMessage}
              handleIngredientAdd={handleIngredientAdd}
              handleIngredientQuantityChange={handleIngredientQuantityChange}
              handleIngredientUnitChange={handleIngredientUnitChange}
              handleIngredientDelete={handleIngredientDelete}
            />
            <div className='mt-3 flex justify-between'>
              <NavigationButton
                validationErrorMessages={[ingredientsTabErrorMessage]}
                onPress={handlePreviousTab}
                shouldGoNext={false}
              />
              <NavigationButton
                validationErrorMessages={[ingredientsTabErrorMessage]}
                onPress={handleNextTab}
                shouldGoNext={true}
              />
            </div>
          </>
        </Tab>
        <Tab key={tabs[2]} title={tabs[2]}>
          <>
            <InstructionsTab
              instructions={recipe.instructions}
              handleAddInstruction={handleAddInstruction}
              handleUpdateInstruction={handleUpdateInstruction}
              handleDeleteInstruction={handleDeleteInstruction}
            />
            <div className='mt-3 flex justify-between'>
              <NavigationButton
                validationErrorMessages={[instructionsTabErrorMessage]}
                onPress={handlePreviousTab}
                shouldGoNext={false}
              />
              <Button
                color='primary'
                isDisabled={validationErrorMessages.some((e) => e !== '')}
                onPress={handleSubmitRecipe}
              >
                {initialRecipeId ? 'Update' : 'Create'}
              </Button>
            </div>
          </>
        </Tab>
      </Tabs>
    </>
  );
}
