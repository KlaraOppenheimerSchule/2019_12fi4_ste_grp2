import { shallowMount, mount } from "@vue/test-utils";
import NumberInput from "@/components/NumberInput.vue";


describe("NumberInput.vue", () => {
    it("renders label", () => {
        const wrapper = shallowMount(NumberInput,{});
        expect(wrapper.text()).toMatch("Schülernummer");
    });
    it("Testing Input Event", () => {
        const num = 123456;
        const wrapper = mount(NumberInput, {});
        const numinput = wrapper.find('.numberInput');
        numinput.value = num;
        numinput.trigger("input");
        expect(numinput.value).toBe(num);
    });
    it("Get Route to ID - correct number", () => {
        const spy = jest.fn();
        const num = 123456;
        const wrapper = mount(NumberInput, {});
        wrapper.vm.$router = {}
        wrapper.vm.$router.push = spy;
        wrapper.vm.val = num;
        wrapper.vm.routeToId();
        expect(spy).toHaveBeenCalledWith({ path: "stats/123456" });
    });
    it("Get Route to ID - incorrect number", () => {
        const spy = jest.fn();
        const num = 1234567;
        const wrapper = mount(NumberInput, {});
        wrapper.vm.val = num;
        wrapper.vm.routeToId();
        expect(wrapper.vm.error).toBe(true);
    });
});
