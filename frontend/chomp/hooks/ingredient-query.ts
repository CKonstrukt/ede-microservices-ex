import { Session } from 'next-auth';
import { useEffect, useState } from 'react';
import { getIngredients } from '@/lib/api/ingredient-service';

export function useIngredientQuery(data: Session | null) {
  const [ingredients, setIngredients] = useState<DbIngredient[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!data) return;

    if (!data?.bearer_token) return;

    getIngredients(data?.bearer_token)
      .then((res) => setIngredients(res))
      .then(() => setLoading(false))
      .catch((error) => {
        setLoading(false);
        console.log(error);
      });
  }, [data]);

  return {
    ingredients,
    loading,
    error,
  };
}
