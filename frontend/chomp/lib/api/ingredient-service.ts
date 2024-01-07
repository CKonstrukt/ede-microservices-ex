import axios from 'axios';

export async function getIngredients(bearer_token: string) {
  const { data } = await axios.get<DbIngredient[]>(
    `${process.env.NEXT_PUBLIC_API_BASE_URL}/ingredient`,
    { headers: { Authorization: `Bearer ${bearer_token}` } }
  );

  return data;
}
