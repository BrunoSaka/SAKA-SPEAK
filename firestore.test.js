const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read user profiles", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Unauthenticated user: cannot write practice session", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("practices").doc("p1").set({
    userId: ALICE_UID,
    topicTitle: "Kepemimpinan",
    category: "Opini",
    durationSeconds: 60,
    transcript: "Halo semuanya...",
    overallScore: 85,
    createdAt: new Date(),
  }));
});

test("Authenticated user: can create own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).set({
    userId: ALICE_UID,
    displayName: "Alice",
    email: "alice@example.com",
    createdAt: new Date(),
    totalPractices: 0,
  }));
});

test("Authenticated user: cannot create profile for another user", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).set({
    userId: BOB_UID,
    displayName: "Bob",
    email: "bob@example.com",
    createdAt: new Date(),
  }));
});

test("Authenticated user: can create valid practice session", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("practices").doc("session_1").set({
    userId: ALICE_UID,
    topicTitle: "Pentingnya Retorika",
    category: "Pendidikan",
    durationSeconds: 120,
    transcript: "Selamat pagi rekan-rekan sekalian...",
    overallScore: 88,
    scoreLanguage: 90,
    scoreArticulation: 85,
    scoreTone: 84,
    scoreImprovisation: 86,
    scoreInsight: 92,
    feedbackSummary: "Penyampaian terstruktur dan artikulasi jelas.",
    feedbackStrengths: ["Kosakata baku", "Tempo stabil"],
    feedbackImprovements: ["Variasi intonasi"],
    createdAt: new Date(),
  }));
});

test("Cross-user access: Bob cannot read Alice's practice session", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).collection("practices").doc("session_alice").set({
      userId: ALICE_UID,
      topicTitle: "Private Alice Speech",
      category: "Personal",
      durationSeconds: 45,
      transcript: "Rahasia...",
      overallScore: 80,
      createdAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("practices").doc("session_alice").get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("practices").get());
});

test("Client query alignment: Alice can list her own practices", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).collection("practices").doc("s1").set({
      userId: ALICE_UID,
      topicTitle: "Speech 1",
      category: "Teknologi",
      durationSeconds: 60,
      transcript: "Teknologi masa kini...",
      overallScore: 82,
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("practices").get());
});
