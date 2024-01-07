import type { NextAuthOptions } from 'next-auth';
import GoogleProvider from 'next-auth/providers/google';
import axios from 'axios';

export const options: NextAuthOptions = {
  providers: [
    GoogleProvider({
      clientId: process.env.GOOGLE_ID!,
      clientSecret: process.env.GOOGLE_SECRET!,
    }),
  ],
  callbacks: {
    async signIn({ user }) {
      try {
        await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/user`, {
          googleId: user?.id,
          email: user?.email,
          name: user?.name,
          imageUrl: user?.image,
        });
      } catch (err: any) {
        console.log('Error creating user');
        return false;
      }
      return true;
    },
    async jwt({ token, account }) {
      if (account) {
        token.id_token = account.id_token;
      }
      return token;
    },
    async session({ session, token, user }) {
      session.bearer_token = token.id_token as string;
      return session;
    },
  },
};
