"use client"

import { useEffect, useState } from "react";
import Link from 'next/link';
import styles from "./page.module.css";
import api from "@/lib/api";

export default function Home() {

  const [me, setMe] = useState<string>('');

  useEffect(() => {
    api.get('/api/ui/main')
      .then((response) => {
        setMe(response.data);
      });
  }, []);

  return (
    <>
      <head>
        <title>Login</title>
      </head>
      <body>

        <Link href="/">Home</Link>

        Hello {me}

      </body>
    </>

  );
}
