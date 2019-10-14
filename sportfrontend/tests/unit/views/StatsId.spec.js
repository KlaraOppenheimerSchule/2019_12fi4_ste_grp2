import { mount } from "@vue/test-utils";
import StatsId from "@/views/StatsId.vue";


describe("StatsId.vue", () => {
  it("Correct Parameter", () => {
    const wrapper = mount(StatsId, {});
    const wrapperRoute = {params:{id: 123456}}
    wrapper.vm.$route = wrapperRoute;
    expect(wrapper.vm.id).toBe(123456);
  });
  it("Inorrect Parameter", () => {
    const wrapper = mount(StatsId, {});
    expect(wrapper.vm.id).toBe(0);
  });

});
