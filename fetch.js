const URL = Java.type("java.net.URL");
const InputStreamReader = Java.type("java.io.InputStreamReader");
const BufferedReader = Java.type("java.io.BufferedReader");

export function fetch(link) {
  try {
    const url = new URL(link);
    const connection = url.openConnection();

    connection.setRequestMethod("GET");
    connection.setRequestProperty("User-Agent", "Mozilla/5.0");

    const reader = new BufferedReader(
      new InputStreamReader(connection.getInputStream())
    );
    let line;
    let response = "";

    while ((line = reader.readLine()) !== null) {
      response += line;
    }

    reader.close();

    try {
      const parsed = JSON.parse(response);
      return parsed;
    } catch (_) {
      return response;
    }
  } catch (err) {
    return "fetch error: " + err;
  }
}
