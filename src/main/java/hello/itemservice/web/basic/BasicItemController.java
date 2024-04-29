package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {

    private final ItemRepository itemRepository;

    //상품 목록
    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "/basic/items";
    }

    //상품 검색
    @GetMapping("/{itemId}")
    public String item(@PathVariable(name = "itemId") long itemId, Model model) { //@PathVariable 어노테이션으로 url에서 itemId를 받아와서 long 타입의 itemId 매개변수로 넘김, model 객체 생성
        Item item = itemRepository.findById(itemId); // itemRepository의 findById 메서드의 인자로 itemId를 줘서 해당 itemId로 검색하여 Item 객체에 저장
        model.addAttribute("item", item); // addAttribute 메서드의 매개변수로 위에서 저장한 item을 item이라는 이름으로 넘겨줌
        return "/basic/item"; // /basic/item 경로로 이동
    }

    @GetMapping("/add")
    public String addForm() {
        return "/basic/addForm";
    }

    @PostMapping("/add")
    public String save() {
        return "/basic/addForm";
    }

    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void init() {
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }
}
