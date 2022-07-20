enum Message {
    Quit,
    Move { x: i32, y: i32 },
    ChangeColor(i32, i32, i32),
}

fn main() {

    
    let m1 = Message::ChangeColor(0, 0, 0);
    if let Message::ChangeColor(a, b, c) = m1 {
        println!("{}", a);
        println!("{}", b);
        println!("{}", c);
    }
    
    let m2 = Message::Move {x : 1, y : 2};
    if let Message::Move{ x: a, y: b} = m2 {
        println!("{}", a);
        println!("{}", b);
    }
}
