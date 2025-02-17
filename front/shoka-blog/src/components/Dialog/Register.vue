<template>
  <n-modal class="bg" v-model:show="dialogVisible" preset="dialog" :show-icon="false" transform-origin="center"
    :block-scroll="false">
    <n-input class="mt-11" placeholder="手机号" v-model:value="registerForm.username"></n-input>
    <n-input-group class="mt-11">
      <n-input placeholder="验证码" v-model:value="registerForm.code"></n-input>
      <n-button color="#49b1f5" :disabled="flag" @click="sendCode">
        {{ timer == 0 ? '发送' : `${timer}s` }}
      </n-button>
    </n-input-group>
    <n-input class="mt-11" type="password" show-password-on="click" placeholder="密码"
      v-model:value="registerForm.password"></n-input>
    <n-button ref="registerRef" class="mt-11" color="#e9546b" style="width:100%" :loading="loading"
      @click="handleRegister">
      注册
    </n-button>
    <div class="mt-10"><span class="dialog-text">已有账号？</span><span class="colorFlag" @click="handleLogin">登录</span>
    </div>
  </n-modal>
</template>

<script setup lang="ts">
import { getCode, login, register } from "@/api/login";
import { LoginForm } from "@/api/login/types";
import { UserForm } from "@/model";
import useStore from "@/store";
import { setToken } from "@/utils/token";
import { useIntervalFn } from '@vueuse/core';
const { app, user } = useStore();
const registerRef = ref();
const data = reactive({
  timer: 0,
  flag: false,
  loading: false,
  registerForm: {
    username: "",
    password: "",
    code: "",
  } as UserForm,
});
const { timer, flag, loading, registerForm } = toRefs(data);
const { pause, resume } = useIntervalFn(() => {
  timer.value--;
  if (timer.value <= 0) {
    // 停止定时器
    pause();
    flag.value = false;
  }
}, 1000, { immediate: false });
const start = (time: number) => {
  flag.value = true;
  timer.value = time;
  // 启动定时器
  resume();
};
const sendCode = () => {
  let reg = /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/;
  if (!reg.test(registerForm.value.username)) {
    window.$message?.warning("手机号格式不正确");
    return;
  }
  start(60);
  getCode(registerForm.value.username).then(({ data }) => {
    if (data.flag) {
      window.$message?.success("发送成功");
    }
  });
};
const handleRegister = () => {
  if (registerForm.value.code.trim().length != 6) {
    window.$message?.warning("请输入6位验证码");
    return;
  }
  if (registerForm.value.password.trim().length < 6) {
    window.$message?.warning("密码不能少于6位");
    return;
  }
  loading.value = true;
  register(registerForm.value).then(({ data }) => {
    if (data.flag) {
      let loginForm: LoginForm = {
        username: registerForm.value.username,
        password: registerForm.value.password,
      }
      login(loginForm).then(({ data }) => {
        if (data.flag) {
          registerForm.value = {
            username: "",
            password: "",
            code: "",
          }
          setToken(data.data);
          user.GetUserInfo();
          window.$message?.success("登录成功");
          app.setRegisterFlag(false);
        }
      });
    }
    loading.value = false;
  });
};
const dialogVisible = computed({
  get: () => app.registerFlag,
  set: (value) => app.registerFlag = value,
});
const handleLogin = () => {
  app.setRegisterFlag(false);
  app.setLoginFlag(true);
};
</script>

<style scoped></style>
