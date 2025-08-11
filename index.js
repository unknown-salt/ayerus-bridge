import { fetch } from "./fetch";

const bots = [
  "c003e39e77cb4ac3ae2fd3d6e2090731",
  "693cd916c7084b3c9b1a80f3669f4842",
];

let names = [];

let regex = new RegExp();

try {
  // prettier-ignore
  for (const id of bots) {
    const data = fetch(`https://api.minecraftservices.com/minecraft/profile/lookup/${id}`);
    names.push(data.name);
  }

  const botPattern = names.join("|");

  // prettier-ignore
  regex = RegExp(`^(?:.{2,4})?(?:Guild|G) (?:.{2,4})?> (?:\\[.+?\\] )?(?:&7)?(?:${botPattern}) (?:.{2,4})?\\[(?:.+?)\\](?:.{2,4})?: (?:&r)?(.+?):(?: )?(.*)$`)

  ChatLib.chat("&7Successfully received bot names!");
} catch (err) {
  console.log(err);
  ChatLib.chat("&cError getting bot names!");
}

register("chat", (discord, message = "", event) => {
  cancel(event);

  // prettier-ignore
  ChatLib.chat(`&3Discord > &b${discord.replace(/&r/g, '§r').replace(/&/g, "§&&b&§b")}&7: ${message.replace(/&r/g, '§r').replace(/&/g, "§&&7&§7")}`);
}).setCriteria(regex);
