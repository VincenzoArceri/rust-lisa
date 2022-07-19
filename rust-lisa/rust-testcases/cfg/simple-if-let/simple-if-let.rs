enum Message {
    Quit,
    Move { x: i32, y: i32 },
    Write(&str),
    ChangeColor(i32, i32, i32),
}

fn main() {
    let m = Message::ChangeColor(0, 0, 0);

    if let Message::ChangeColor(a, b, c) = m {
        println!("{}", a);
    } else {
        return;
    }
}
