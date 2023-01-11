import { reactive, readonly } from "vue";
import type { ILoginResponse } from "./ILoginResponse";
import type { ILoginState } from "./ILoginState";
import type { ISendLogin } from "./ISendLogin";

/**
 * global State for user
 * @author Tom Gouthier
 * @author Marie Bohnert
 */
const loginState: ILoginState = reactive({
  username: "",
  avatar: "src/assets/avatar/Schwein.png",
  error: "",
  loggedIn: false,
});

/**
 * @author Tom Gouthier,
 * @author Marie Bohnert
 * sets Avatar to certain image url
 */

const setAvatar = (avatar: string) => {
  loginState.avatar = avatar;
  console.log(avatar);
};

/**
 * @author Tom Gouthier
 * @author Marie Bohnert
 * sends login request to server
 */

async function login(username: string) {
  if (loginState.loggedIn) {
    loginState.error = `Already signed in as ${loginState.username}`;
    return;
  }
  // if username too short dont make the request at all
  if (username.length >= 3) {
    const login: ISendLogin = {
      name: username,
    };

    console.log(login);
    const url = "/api/user/login";
    try {
      const response = await fetch(url, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(login),
      });

      if (!response.ok) {
        throw new Error(response.statusText);
      } else {
        const jsondata: ILoginResponse = await response.json();
        console.log(jsondata);
        // when no error sent by server, set loginState accordingly
        if (jsondata.error === "") {
          loginState.error = "";
          loginState.username = jsondata.username;
          loginState.loggedIn = true;
          console.log("logged in as", username);
          // else dont do a login and reset State, set Error to error sent by server
        } else {
          loginState.error = jsondata.error;
        }
      }
    } catch (reason: any) {
      loginState.error = reason;
    }
  } else {
    loginState.error = "Name too Short. Has to be 3 or above letters long.";
  }
}

/**
 * @author Tom Gouthier
 * @author Marie Bohnert
 * logout and reset State
 */
async function logout() {
  if (!loginState.loggedIn) {
    return;
  }
  const url = `/api/user/logout?username=${loginState.username}`;
  try {
    const response = await fetch(url, {
      method: "DELETE",
    });
    if (!response.ok) {
      console.log(response.text);
    } else {
      console.log(`${loginState.username} logged out`);
      loginState.username = "";
      loginState.loggedIn = false;
      loginState.error = "";
    }
  } catch (error) {
    console.log(error);
  }

  console.log(loginState);
}

export function useLogin() {
  return {
    loginData: readonly(loginState),
    setAvatar,
    login,
    logout,
  };
}
