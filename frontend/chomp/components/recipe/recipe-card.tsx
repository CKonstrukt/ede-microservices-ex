'use client';

import {
  Button,
  Card,
  CardBody,
  CardFooter,
  CardHeader,
  Chip,
  Dropdown,
  DropdownItem,
  DropdownMenu,
  DropdownTrigger,
  Link,
  Modal,
  ModalBody,
  ModalContent,
  ModalFooter,
  ModalHeader,
  useDisclosure,
} from '@nextui-org/react';
import { DbRecipe } from '@/lib/api/models/recipe';
import { FaEllipsisVertical, FaRegClock, FaUserGroup } from 'react-icons/fa6';
import {
  extractHours,
  extractMinutes,
} from '@/lib/helpers/recipe-duration-helper';

function RecipeCardDeleteModal(props: {
  id: number;
  name: string;
  isOpen: boolean;
  onOpenChange: () => void;
  handleDelete: (arg0: number) => void;
}) {
  function handleConfirm(closeFn: () => void) {
    closeFn();
    props.handleDelete(props.id);
  }

  return (
    <Modal
      isOpen={props.isOpen}
      onOpenChange={props.onOpenChange}
      isDismissable={false}
    >
      <ModalContent>
        {(onClose) => (
          <>
            <ModalHeader className='flex flex-col gap-1'>
              Are you sure?
            </ModalHeader>
            <ModalBody>
              <p>Do you really want to delete the recipe {props.name}?</p>
            </ModalBody>
            <ModalFooter>
              <Button color='danger' variant='light' onPress={onClose}>
                Cancel
              </Button>
              <Button color='danger' onPress={() => handleConfirm(onClose)}>
                Delete
              </Button>
            </ModalFooter>
          </>
        )}
      </ModalContent>
    </Modal>
  );
}

function RecipeCardDropdown(props: {
  id: number;
  onOpenDeleteModal: () => void;
}) {
  return (
    <Dropdown>
      <DropdownTrigger>
        <Button isIconOnly variant='light' size='sm'>
          <FaEllipsisVertical />
        </Button>
      </DropdownTrigger>
      <DropdownMenu aria-label='Instruction actions'>
        <DropdownItem as={Link} key='Edit' href={`/recipe/manage/${props.id}`}>
          Edit
        </DropdownItem>
        <DropdownItem
          key='Delete'
          className='text-danger'
          color='danger'
          onPress={props.onOpenDeleteModal}
        >
          Delete
        </DropdownItem>
      </DropdownMenu>
    </Dropdown>
  );
}

export default function RecipeCard({
  recipe,
  handleDelete,
}: {
  recipe: DbRecipe;
  handleDelete: (arg0: number) => void;
}) {
  const { isOpen, onOpen, onOpenChange } = useDisclosure();

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
    <Card>
      <CardBody>
        <div>
          <h2 className='text-2xl'>{recipe.name}</h2>
          <div className='flex gap-4'>
            <Chip
              color='secondary'
              variant='flat'
              startContent={<FaUserGroup />}
            >
              {recipe.amountOfPeople + ' '}
            </Chip>
            <Chip
              color='secondary'
              variant='flat'
              startContent={<FaRegClock />}
            >
              {durationHours}
              {durationMinutes}
            </Chip>
          </div>
          <div className='mt-3'>
            {!!recipe.description ? (
              <p className='truncate'>{recipe.description}</p>
            ) : (
              <p>No description provided</p>
            )}
          </div>
        </div>
      </CardBody>
      <CardFooter className='flex justify-between'>
        <Link href={`/recipe/${recipe.id}`}>View</Link>
        <div>
          <RecipeCardDropdown id={recipe.id} onOpenDeleteModal={onOpen} />
          <RecipeCardDeleteModal
            id={recipe.id}
            name={recipe.name}
            isOpen={isOpen}
            onOpenChange={onOpenChange}
            handleDelete={handleDelete}
          />
        </div>
      </CardFooter>
    </Card>
  );
}
