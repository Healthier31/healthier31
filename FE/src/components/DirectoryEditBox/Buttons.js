import { styled } from "styled-components";

import {
  CANCEL_TEXT,
  COMMENT_POST_BUTTON,
  FEED_MODIFY_TEXT,
} from "../../data/constants";

const Buttons = ({ EditPage }) => {
  return (
    <>
      <ButtonsSection>
        <Button>{EditPage ? COMMENT_POST_BUTTON : FEED_MODIFY_TEXT}</Button>
        <Button>{CANCEL_TEXT}</Button>
      </ButtonsSection>
    </>
  );
};

export default Buttons;

const ButtonsSection = styled.section`
  width: 100%;

  display: flex;
  flex-direction: row;
  justify-content: center;
`;

const Button = styled.button`
  margin: 20px 15px;
  padding: 10px 30px;
  border: 1px solid #d0d0d0;
  border-radius: 15px;

  &:hover {
    background-color: #d0d0d0;
  }
`;
