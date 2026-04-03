# SentinelBet — Auth Setup Guide

## Stack: Supabase Auth (FREE) + Google Sign-In (Credential Manager)

Custo total: **R$ 0,00** até ~50.000 usuários ativos.

---

## 1. Criar projeto no Supabase

1. Acesse https://supabase.com e crie um projeto gratuito
2. Copie os valores de **Settings → API**:
   - `Project URL` → `SUPABASE_URL`
   - `anon / public key` → `SUPABASE_ANON_KEY`
3. Preencha em `app/src/main/java/com/sentinelbet/app/data/remote/SupabaseConfig.kt`

---

## 2. Configurar Google Sign-In

### No Google Cloud Console
1. Acesse https://console.cloud.google.com
2. Crie um OAuth 2.0 Client ID do tipo **Web Application**
3. Copie o Client ID → `GOOGLE_WEB_CLIENT_ID` em `SupabaseConfig.kt`
4. Crie também um Client ID do tipo **Android** com o SHA-1 do seu keystore

### No Supabase Dashboard
1. Auth → Providers → Google
2. Ative e cole o Web Client ID e o Client Secret
3. Em **URL Configuration → Redirect URLs**, adicione:
   ```
   sentinelbet://login-callback
   ```

---

## 3. Criar tabela de subscriptions no Supabase

Execute no SQL Editor do Supabase:

```sql
create table if not exists public.subscriptions (
  user_id    uuid primary key references auth.users(id) on delete cascade,
  plan       text not null default 'free' check (plan in ('free', 'pro')),
  expires_at timestamptz,
  created_at timestamptz default now()
);

-- Habilitar Row Level Security
alter table public.subscriptions enable row level security;

-- Usuário só lê a própria linha
create policy "Users read own subscription"
  on public.subscriptions for select
  using (auth.uid() = user_id);

-- Apenas funções internas (Edge Functions / service_role) podem inserir/atualizar
create policy "Service role manages subscriptions"
  on public.subscriptions for all
  using (auth.role() = 'service_role');
```

---

## 4. Feature Gates implementados

| Feature                  | Plano mínimo | Arquivo de controle          |
|--------------------------|--------------|------------------------------|
| Dashboard                | FREE         | SubscriptionRepository.kt    |
| Bankroll                 | FREE         | SubscriptionRepository.kt    |
| Histórico básico         | FREE         | SubscriptionRepository.kt    |
| Live básico              | FREE         | SubscriptionRepository.kt    |
| Value Bets (3 primeiras) | FREE         | ValueBetsScreen.kt           |
| Análise IA               | **PRO**      | AppNavHost.kt                |
| Value Bets ilimitadas    | **PRO**      | ValueBetsScreen.kt           |
| Live Premium             | **PRO**      | SubscriptionRepository.kt    |
| Exportação de histórico  | **PRO**      | SubscriptionRepository.kt    |

---

## 5. Próximos passos (BILL-01)

Para processar pagamentos e ativar o plano PRO, implemente:
- **Google Play Billing** (billing:7.0.0) para pagamentos in-app
- Ou **Stripe** via WebView/Custom Tab para pagamentos web
- Ao confirmar pagamento: chamar Supabase Edge Function que faz `upsert` na tabela `subscriptions` com `plan = 'pro'`

Edge Function gratuita no Supabase Free tier: até 500.000 invocações/mês.
