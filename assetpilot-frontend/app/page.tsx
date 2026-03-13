type HealthResponse = {
  status: string;
};

type Account = {
  id: number;
  name: string;
  provider: string;
  type: string;
  currency: string;
  currentBalance: number;
};



async function getAccounts(): Promise<Account[]> {
  const response = await fetch("http://localhost:8080/api/accounts", {
    cache: "no-store",
  });

  if (!response.ok) {
    throw new Error("Accounts konnten nicht geladen werden");
  }

  return response.json();
}

export default async function Home() {
 
  const accounts = await getAccounts();

  return (
    <main className="min-h-screen bg-zinc-50 p-8 text-zinc-900">
      <div className="mx-auto max-w-5xl">
        <header className="mb-8">
          <h1 className="text-4xl font-bold">AssetPilot</h1>
          <p className="mt-2 text-zinc-600">
            Personal Finance & Portfolio Dashboard
          </p>
        </header>

       

        <section className="rounded-2xl border border-zinc-200 bg-white p-6 shadow-sm">
          <h2 className="mb-4 text-xl font-semibold">Konten</h2>

          {accounts.length === 0 ? (
            <p className="text-zinc-600">Noch keine Konten vorhanden.</p>
          ) : (
            <div className="grid gap-4 sm:grid-cols-2">
              {accounts.map((account) => (
                <div
                  key={account.id}
                  className="rounded-xl border border-zinc-200 p-4"
                >
                  <h3 className="text-lg font-semibold">{account.name}</h3>
                  <p className="text-sm text-zinc-500">{account.provider}</p>
                  <p className="mt-2 text-sm">Typ: {account.type}</p>
                  <p className="mt-1 text-lg font-bold">
                    {account.currentBalance} {account.currency}
                  </p>
                </div>
              ))}
            </div>
          )}
        </section>
      </div>
    </main>
  );
}