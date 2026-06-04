import { Router } from "express";
import { rebootAllServices } from "../services/reboot.service";

const router = Router();

router.get("/reboot", async (req, res) => {
  try {
    await rebootAllServices();
    return res.status(200).send();
  } catch (error) {
    console.error("Erro no reboot:", error);
    return res.status(500).send();
  }
});

export default router;