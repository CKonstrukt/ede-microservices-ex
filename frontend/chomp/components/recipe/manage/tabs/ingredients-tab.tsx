'use client';

import {
  Autocomplete,
  AutocompleteItem,
  Button,
  Card,
  CardBody,
  Input,
  Select,
  Selection,
  SelectItem,
  Skeleton,
  Table,
  TableBody,
  TableCell,
  TableColumn,
  TableHeader,
  TableRow,
} from '@nextui-org/react';
import { useState } from 'react';
import { Session } from 'next-auth';
import { Key } from '@react-types/shared';
import { MdDelete } from 'react-icons/md';
import { IconContext } from 'react-icons';
import { useIngredientQuery } from '@/hooks/ingredient-query';

function IngredientAutocomplete(props: {
  ingredients: DbIngredient[];
  handleAdd: (arg0: DbIngredient) => boolean;
}) {
  const [autocompleteValue, setAutocompleteValue] = useState('');
  const [errorMessage, setErrorMessage] = useState<String | undefined>();

  function handleAutocompleteChange(key: Key) {
    setErrorMessage(undefined);

    const ingredient = props.ingredients.find((i) => i.id === key);
    if (!ingredient) return;

    if (!props.handleAdd(ingredient)) {
      setErrorMessage("You can't add the same ingredient twice.");
      return;
    }

    setAutocompleteValue(ingredient.id);
  }

  return (
    <div className='mb-5'>
      <Autocomplete
        defaultItems={props.ingredients}
        label='Ingredients'
        placeholder='Search for ingredients'
        className='max-w-sm'
        selectedKey={autocompleteValue === '' ? undefined : autocompleteValue}
        onSelectionChange={handleAutocompleteChange}
      >
        {(ingredient) => (
          <AutocompleteItem key={ingredient.id}>
            {ingredient.name}
          </AutocompleteItem>
        )}
      </Autocomplete>
      {!!errorMessage && <p className='text-red-500'>{errorMessage}</p>}
    </div>
  );
}

function IngredientTableQuantityCell(props: {
  initialQuantity?: number;
  handleQuantityChange: (arg1: number) => void;
}) {
  const [quantity, setQuantity] = useState(
    props.initialQuantity?.toString() || ''
  );

  function handleQuantityInputChange(newValue: string) {
    props.handleQuantityChange(+newValue);
    setQuantity(newValue);
  }

  return (
    <Input
      type='number'
      label='Quantity'
      size='sm'
      value={quantity}
      onValueChange={handleQuantityInputChange}
      className='w-32'
    />
  );
}

function IngredientTableUnitCell(props: {
  initialUnit: string;
  units: string[];
  handleUnitChange: (arg0: string) => void;
}) {
  const [unit, setUnit] = useState(!props.initialUnit ? '' : props.initialUnit);

  function handleUnitInputChange(keys: Selection) {
    const mySet = new Set(keys);
    let firstKey = mySet.values().next();

    props.handleUnitChange(firstKey.value);
    setUnit(firstKey.value);
  }

  return (
    <Select
      name='unit'
      label='Unit'
      size='sm'
      className='w-40'
      selectedKeys={unit === '' ? undefined : [unit]}
      onSelectionChange={handleUnitInputChange}
    >
      {props.units.map((unit) => (
        <SelectItem key={unit} value={unit}>
          {unit}
        </SelectItem>
      ))}
    </Select>
  );
}

function IngredientTableDeleteCell({
  handleDelete,
}: {
  handleDelete: () => void;
}) {
  return (
    <Button
      isIconOnly
      onPress={handleDelete}
      color='danger'
      aria-label='delete'
      size='md'
    >
      <IconContext.Provider value={{ className: 'size-4' }}>
        <MdDelete />
      </IconContext.Provider>
    </Button>
  );
}

function IngredientTableCell(props: {
  ingredient: DbIngredient;
  columnKey: Key;
  handleIngredientQuantityChange: (arg0: string, arg1: number) => void;
  handleIngredientUnitChange: (arg0: string, arg1: string) => void;
  handleIngredientDelete: (arg0: string) => void;
}) {
  const id = props.ingredient.id;

  function handleQuantityChange(quantity: number) {
    props.handleIngredientQuantityChange(id, quantity);
  }

  function handleUnitChange(unit: string) {
    props.handleIngredientUnitChange(id, unit);
  }

  function handleDelete() {
    props.handleIngredientDelete(id);
  }

  switch (props.columnKey) {
    case columns[1].key:
      return (
        <IngredientTableQuantityCell
          initialQuantity={props.ingredient.quantity}
          handleQuantityChange={handleQuantityChange}
        />
      );
    case columns[2].key:
      return (
        <IngredientTableUnitCell
          initialUnit={props.ingredient.unit}
          units={props.ingredient.units}
          handleUnitChange={handleUnitChange}
        />
      );
    case columns[3].key:
      return <IngredientTableDeleteCell handleDelete={handleDelete} />;
  }

  return <p>{props.ingredient.name}</p>;
}

const columns = [
  {
    key: 'Name',
    label: 'Name',
  },
  {
    key: 'Quantity',
    label: 'Quantity',
  },
  {
    key: 'Unit',
    label: 'Unit',
  },
  {
    key: 'Action',
    label: 'Action',
  },
];

function addUnitsToSelectedIngredients(
  selectedIngredients: DbIngredient[],
  ingredients: DbIngredient[]
) {
  return selectedIngredients.map((ingredient) => {
    if (ingredient.units) return ingredient;

    const correspondingIngredient = ingredients.find(
      (i) => i.id === ingredient.id
    );
    return {
      ...ingredient,
      units: correspondingIngredient!.units,
    };
  });
}

export default function IngredientsTab(props: {
  data: Session | null;
  selectedIngredients: DbIngredient[];
  validationErrorMessage: string;
  handleIngredientAdd: (arg0: DbIngredient) => void;
  handleIngredientQuantityChange: (arg0: string, arg1: number) => void;
  handleIngredientUnitChange: (arg0: string, arg1: string) => void;
  handleIngredientDelete: (arg0: string) => void;
}) {
  const { ingredients, loading, error } = useIngredientQuery(props.data);

  if (loading) return <Skeleton className='h-40 w-full' />;

  function handleAdd(newIngredient: DbIngredient): boolean {
    if (selectedIngredients.find((i) => i.id === newIngredient.id)) {
      return false;
    }

    props.handleIngredientAdd(newIngredient);
    return true;
  }

  function handleDelete(id: string) {
    props.handleIngredientDelete(id);
  }

  const selectedIngredients = addUnitsToSelectedIngredients(
    props.selectedIngredients,
    ingredients
  );

  return (
    <Card>
      <CardBody>
        <IngredientAutocomplete
          ingredients={ingredients}
          handleAdd={handleAdd}
        />
        <Table hideHeader aria-label='Ingredients table'>
          <TableHeader columns={columns}>
            {(column) => (
              <TableColumn key={column.key}>{column.label}</TableColumn>
            )}
          </TableHeader>
          <TableBody
            items={selectedIngredients}
            emptyContent={'Add some ingredients to get started'}
          >
            {(item) => (
              <TableRow key={item.id}>
                {(columnKey) => (
                  <TableCell>
                    <IngredientTableCell
                      ingredient={item}
                      handleIngredientQuantityChange={
                        props.handleIngredientQuantityChange
                      }
                      handleIngredientUnitChange={
                        props.handleIngredientUnitChange
                      }
                      handleIngredientDelete={handleDelete}
                      columnKey={columnKey}
                    />
                  </TableCell>
                )}
              </TableRow>
            )}
          </TableBody>
        </Table>
        {!props.validationErrorMessage.endsWith('required') && (
          <p className='mt-2 text-center text-red-500'>
            {props.validationErrorMessage}
          </p>
        )}
      </CardBody>
    </Card>
  );
}
