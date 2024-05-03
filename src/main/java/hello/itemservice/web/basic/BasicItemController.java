package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

//    @PostMapping("/add") //addItemV2 만들기 위해 주석처리
    public String addItemV1(@RequestParam String itemName, // form에서 post 방식으로 넘어오는 데이터 중 name=itemName 값을 받기 위한 어노테이션
                       @RequestParam int price, // form에서 post 방식으로 넘어오는 데이터 중 name=prince 값을 받기 위한 어노테이션
                       @RequestParam Integer quantity, 
                       Model model) {

        Item item = new Item(); // Item의 인스턴스 item 생성
        item.setItemName(itemName); // @RequestParam으로 받아온 itemName의 값을 item 인스턴스의 itemName으로 저장
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item); // 생성 및 itemNae, price, quantity를 설정한 item 인스턴스를 리포지토리에 저장

        model.addAttribute("item", item); // item 인스턴스를 model에 전달

        return "/basic/item";
    }

//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item, Model model) { // 3. Model model은 생략해도 됨
        
        // 1. @ModelAttribute 가 form에서 넘어온 데이터를 받아서 자동으로 item 인스턴스를 생성하고, set을 해주므로 인스턴스 생성과 set 코드 생략

        itemRepository.save(item);
//        model.addAttribute("item", item); // 2. @ModelAttribute 어노테이션이 어노테이션의 name 속성으로 지정한 이름(여기서는 item)으로 model 객체도 넘겨주기 때문에 생략 가능
        return "/basic/item";
    }

//    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item) { // 어노테이션의 name 속성을 생략하면 클래스(Item)의 첫 글자를 소문자로 바꿔서(item) modelAttribute 에 넣어줌
        itemRepository.save(item);
        return "/basic/item";
    }

//    @PostMapping("/add")
    public String addItemV4(Item item) { // 어노테이션 생략 시, String 타입같은 것이 오면 @RequestParam 자동 적용, 사용자 정의 타입의 클래스가 오면 @ModelAttribute 자동 적용
        itemRepository.save(item);
        return "/basic/item";
    }

    // return 을 redirect 방식으로. Why? -> 상품 저장 후 상품 페이지에서 새로 고침을 하게 되면 마지막 요청(post 방식으로 데이터를 보내서 저장)을 다시 보내게 됨
    // 그 때문에 상품 저장 후 이동한 페이지에서 새로고침을 하게 되면 상품 id가 계속 올라가면서 동일한 내용의 상품이 계속 저장됨
    // 이를 해결하기 위해 return 상품 페이지가 아닌 redirect 로 get 요청을 해줌(PRG 패턴, Post/Redirect/Get)
    // PRG 패턴을 사용한 후 상품 등록 후 새로고침을 하면 마지막 요청(다른 URL로 Get 요청)을 보내게 되므로 잘못된 상품 등록이 되지 않음
//    @PostMapping("/add")
    public String addItemV5(Item item) {
        itemRepository.save(item);
        return "redirect:/basic/items/" + item.getId();
    }

    // 위와 같이 redirect 방식으로 리턴할 때, 위에서는 item.getId 가 숫자여서 괜찮았지만 한글, 혹은 띄어쓰기가 있는 경우에는 인코딩이 필수
    // RedirectAttributes 를 사용하면 URL 인코딩 해줌
    @PostMapping("/add")
    public String addItemV6(Item item, RedirectAttributes redirectAttributes) {
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId()); // 여기서 itemId 라는 이름으로 넘긴 savedItem.getId가 밑의 return 에서 {itemId} 안에 들어감(치환)
        redirectAttributes.addAttribute("status", true); // 치환되지 않은 status 는 쿼리 파라미터 형식으로 URL에 들어감 ex) localhost:8080/basic/items/{itemId}?status=true
        return "redirect:/basic/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}";
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
