import { shallowMount } from "@vue/test-utils";
import "chart.js";
import BarChart from "@/components/BarChart.vue";

jest.mock('chart.js');

describe("BarChart.vue", () => {
  it("test if rendered", () => {
    const id = "kek";
    const wrapper = shallowMount(BarChart, {
      propsData: { id }
    });
    expect(wrapper.html()).toMatch('id="kek"');
  });
});
