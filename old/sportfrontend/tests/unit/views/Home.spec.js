import { shallowMount } from "@vue/test-utils";
import Home from "@/views/Home.vue";

describe("Home.vue", () => {
  it("Renders Headline", () => {
    const wrapper = shallowMount(Home, {});
    expect(wrapper.text()).toMatch("Klara-Oppenheimer-Schule");
  });
});
