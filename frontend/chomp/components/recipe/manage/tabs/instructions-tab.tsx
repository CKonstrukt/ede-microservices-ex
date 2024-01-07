'use client';

import {
  Button,
  Card,
  CardBody,
  Dropdown,
  DropdownItem,
  DropdownMenu,
  DropdownTrigger,
  Textarea,
} from '@nextui-org/react';
import { useState } from 'react';
import { FaEllipsisVertical } from 'react-icons/fa6';

function ListItemDropdown(props: {
  idx: number;
  handleSetNewEditInstruction: (arg0: number) => void;
  handleDeleteInstruction: (arg0: number) => void;
}) {
  return (
    <Dropdown>
      <DropdownTrigger>
        <Button isIconOnly variant='light' size='sm'>
          <FaEllipsisVertical />
        </Button>
      </DropdownTrigger>
      <DropdownMenu aria-label='Instruction actions'>
        <DropdownItem
          key='Edit'
          onPress={() => props.handleSetNewEditInstruction(props.idx)}
        >
          Edit
        </DropdownItem>
        <DropdownItem
          key='Delete'
          className='text-danger'
          color='danger'
          onPress={() => props.handleDeleteInstruction(props.idx)}
        >
          Delete
        </DropdownItem>
      </DropdownMenu>
    </Dropdown>
  );
}

function InstructionInputArea({
  initialValue = '',
  handleSubmit,
  handleCancel,
}: {
  initialValue?: string;
  handleSubmit: (arg0: string) => void;
  handleCancel?: () => void;
}) {
  const [value, setValue] = useState(initialValue);

  function checkIfCanAddNewInstruction() {
    if (value.trim() === '') return;

    handleSubmit(value);
    setValue('');
  }

  return (
    <div className={initialValue ? 'mt-5 grow' : 'mt-5'}>
      <Textarea
        label={initialValue ? 'Edit this step' : 'New step'}
        placeholder={initialValue ? '' : 'Your next step'}
        value={value}
        onValueChange={setValue}
      />
      <div className='flex justify-end'>
        {!!initialValue && (
          <Button
            className='mr-3 mt-2'
            color='danger'
            variant='flat'
            onPress={handleCancel}
          >
            Cancel
          </Button>
        )}
        <Button
          className='mt-2'
          color='primary'
          variant='flat'
          isDisabled={value === ''}
          onPress={checkIfCanAddNewInstruction}
        >
          {initialValue ? 'Done' : 'Add'}
        </Button>
      </div>
    </div>
  );
}

function InstructionList(props: {
  instructions: string[];
  handleUpdateInstruction: (arg0: number, arg1: string) => void;
  handleDeleteInstruction: (arg0: number) => void;
}) {
  const [editIdx, setEditIdx] = useState<number | undefined>();

  function handleSetNewEditInstruction(idx: number) {
    setEditIdx(idx);
  }

  function handleEditSubmit(newValue: string) {
    if (editIdx === undefined) return;

    props.handleUpdateInstruction(editIdx, newValue);
    setEditIdx(undefined);
  }

  function handleEditCancel() {
    setEditIdx(undefined);
  }

  return (
    <ol className='ml-5 list-decimal'>
      {props.instructions.map((instruction, idx) => (
        <li key={idx} className='mt-2'>
          <div className='flex'>
            {editIdx === idx ? (
              <InstructionInputArea
                initialValue={instruction}
                handleSubmit={handleEditSubmit}
                handleCancel={handleEditCancel}
              />
            ) : (
              <p className='grow'>{instruction}</p>
            )}
            <ListItemDropdown
              idx={idx}
              handleSetNewEditInstruction={handleSetNewEditInstruction}
              handleDeleteInstruction={props.handleDeleteInstruction}
            />
          </div>
        </li>
      ))}
    </ol>
  );
}

export default function InstructionsTab(props: {
  instructions: string[];
  handleAddInstruction: (arg0: string) => void;
  handleUpdateInstruction: (arg0: number, arg1: string) => void;
  handleDeleteInstruction: (arg0: number) => void;
}) {
  return (
    <Card>
      <CardBody>
        <h2 className='text-xl'>Add the steps for your recipe.</h2>
        {props.instructions.length > 0 ? (
          <InstructionList
            instructions={props.instructions}
            handleUpdateInstruction={props.handleUpdateInstruction}
            handleDeleteInstruction={props.handleDeleteInstruction}
          />
        ) : (
          <p className='mt-3'>
            No instructions yet, add one via the box below.
          </p>
        )}
        <InstructionInputArea handleSubmit={props.handleAddInstruction} />
      </CardBody>
    </Card>
  );
}
