enum Message {
    Quit,
    Move { x: i32, y: i32 },
    Write(&str),
    ChangeColor(i32, i32, i32),
}

fn main() {
    let m = Message::Write("Hello world!");
    let x = Message::Quit;
    let y = Message::Move { x : 4, y : 2};
    let z = Message::ChangeColor(1, 2, 3);
}
