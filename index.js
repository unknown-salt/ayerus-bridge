import { fetch } from "./fetch";

const bot = "c003e39e77cb4ac3ae2fd3d6e2090731";

let regex = new RegExp();

try {
  // prettier-ignore
  const botName = fetch(`https://api.minecraftservices.com/minecraft/profile/lookup/${bot}`).name;
  // prettier-ignore
  regex = RegExp(`^(?:.{2,4})?(?:Guild|G) (?:.{2,4})?> (?:\\[.+?\\] )?(?:&7)?(?:${botName}) (?:.{2,4})?\\[(?:.+?)\\](?:.{2,4})?: (?:&r)?(.+?):(?: )?(.*)$`)
  ChatLib.chat("&7Successfully received bot name!");
} catch (err) {
  console.log(err);
  ChatLib.chat("&cError getting bot name!");
}

register("chat", (discord, message = "", event) => {
  cancel(event);

  // prettier-ignore
  ChatLib.chat(`&3Discord > &b${discord.replace(/&r/g, '§r').replace(/&/g, "§&&b&§b")}&7: ${message.replace(/&r/g, '§r').replace(/&/g, "§&&7&§7")}`);
}).setCriteria(regex);
