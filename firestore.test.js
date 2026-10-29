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

test("Authenticated user: Alice can read and write her own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).set({
    userId: ALICE_UID,
    email: "alice@example.com",
    displayName: "Alice Sharma",
    targetExam: "OPSC OAS"
  }));
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: Bob cannot read or modify Alice profile", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      email: "alice@example.com"
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).update({ displayName: "Hacked" }));
});

test("Authenticated user: Alice can write and read her mock results", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("mock_results").doc("res_1").set({
    id: "res_1",
    userId: ALICE_UID,
    testId: "opsc_prelims_1",
    testTitle: "OPSC General Studies Mock 1",
    score: 85,
    totalQuestions: 100,
    correctAnswers: 85,
    incorrectAnswers: 15,
    timeTakenSeconds: 3600
  }));
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("mock_results").doc("res_1").get());
});

test("Authenticated user: Bob cannot read Alice mock results", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).collection("mock_results").doc("res_1").set({
      id: "res_1",
      userId: ALICE_UID,
      testId: "opsc_1",
      score: 90,
      totalQuestions: 100
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("mock_results").doc("res_1").get());
});

test("Authenticated user: Can read exam categories catalog", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("exam_categories").doc("opsc").set({
      id: "opsc",
      name: "Odisha Public Service Commission",
      shortCode: "OPSC"
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("exam_categories").doc("opsc").get());
  // Client cannot write to catalog
  await assertFails(aliceDb.collection("exam_categories").doc("opsc").set({
    id: "opsc",
    name: "Modified",
    shortCode: "OPSC"
  }));
});
